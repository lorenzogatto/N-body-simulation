{-
  PAP 2015-2016
  Assignment #2 - Soluzioni
  v 1.0 
-}


import Screen

-- Ex. 1

data Sym = Dot | Dash
data Code = Single Sym | Comp Sym Code

{--
  Strategia: usiamo una funzione di appoggio con cui
  scorriamo gli elementi della lista tenendo traccia
  ma mano della sequenza più lunga trovata
--}
longestDashSeq :: Code -> Maybe Int
longestDashSeq co = longestDashSeq' co 0 0 False
  where
    longestDashSeq' (Single Dot) _ _ False = Nothing
    longestDashSeq' (Single Dot) c m True
      | c < m = Just m
      | otherwise = Just c
    longestDashSeq' (Single Dash) c m _
      | c' < m = Just m
      | otherwise = Just c'
        where c' = c + 1
    longestDashSeq' (Comp Dash co) c m _ = longestDashSeq' co (c+1) m True
    longestDashSeq' (Comp Dot co) c m fo
      | c < m = longestDashSeq' co 0 m fo
      | otherwise = longestDashSeq' co 0 c fo


-- Ex. 2

findMax :: [Int] -> Maybe Int
findMax [] = Nothing
findMax (x:xs) = Just (foldr (\curr max -> if (curr > max) then curr else max) x xs)

-- Ex. 3

type BuyerId = String
type City = String
type Year = Int
type Amount = Int

data Buyer = Buyer BuyerId City
    deriving (Show)

data Transaction = Trans BuyerId  Year Amount
    deriving (Show)

data DBase = DBase [Buyer][Transaction]

buyers = [
  Buyer "maria" "Milano",
  Buyer "stefano" "Roma",
  Buyer "laura" "Cesena",
  Buyer "alice" "Cesena"]

transactions = [
      Trans "alice" 2011 300,
      Trans "maria" 2012 1000,
      Trans "maria" 2011 400,
      Trans "laura" 2012 710,
      Trans "stefano" 2011 700,
      Trans "stefano" 2012 950,
      Trans "alice" 2015 1000,
      Trans "laura" 2016 2000]

db = DBase buyers transactions

qsort :: (a -> a -> Bool) -> [a] -> [a]
qsort _ [] = []
qsort f (x:xs) = qsort f l ++ [x] ++ qsort f r
  where
    l = [ y | y <- xs, f y x]
    r = [ z | z <- xs, not (f z x)]

querySortedTransList :: DBase -> Year -> [Transaction]
querySortedTransList (DBase bs ts) y =
  (qsort s . filter (\(Trans _ y' _) -> y == y')) ts
  where
    s = \(Trans _ _ a1) (Trans _ _ a2) -> a1 < a2

queryBuyerCities :: DBase -> [City]
queryBuyerCities (DBase b _) =
  foldr (\(Buyer _ c) l -> if (not (member c l)) then (c:l) else l) [] b
    where
      member _ [] = False
      member c (x:xs)
        | c == x = True
        | otherwise = member c xs

queryExistBuyerFrom :: DBase -> City -> Bool
queryExistBuyerFrom (DBase bu _) city =
  (foldl (\a b -> a || b) False . map (\(Buyer _ c) -> c == city)) bu

queryAmountsFromCity :: DBase -> City -> Amount
queryAmountsFromCity (DBase bu tr) city =
  (foldl (\s (Trans _ _ a) -> s + a) 0
  .filter (\(Trans n _ _) -> isFromThatCity bu n)) tr
  where
    isFromThatCity [] _ = False
    isFromThatCity ((Buyer b' c'):xs) b
        | b' == b && c' == city = True
        | b' == b = False
        | otherwise = isFromThatCity xs b

-- Ex. 4


data TObj = Text Pos String Color
  | HLine Pos Int Color
  | VLine Pos Int Color
  | Rect Pos Int Int Color
  | Box Pos Int Int Color

class Viewable o where
  render :: o -> Viewport -> IO()
  renderWithClipping :: o -> Viewport ->  IO ()
  renderAll :: Viewport -> [o] ->  IO ()
  renderAllWithClipping :: Viewport -> [o] ->  IO ()

-- aux function to draw with clipping
writeAtWithClipping :: Pos -> String -> Viewport -> IO()
writeAtWithClipping (px,py) s (Viewport (ox,oy) vw vh)
    | px <= vw && py <= vh = writeAt (ox+px-1,oy+py-1) (trunc s (vw-px+1))
    | otherwise = return ()
      where
        trunc _ 0 = ""
        trunc [] _ = ""
        trunc (x:xs) n = x : trunc xs (n-1)

instance Viewable TObj where

  render (Text (px, py) t c) (Viewport (ox,oy) w h) =
      setFColor c >> writeAt (ox+px-1,oy+py-1) t

  render (HLine (px, py) s c) (Viewport (ox,oy) _ _) =
      setFColor c >> writeAt (ox+px-1,oy+py-1) (makeHLine s)
      where
        makeHLine 0 = ""
        makeHLine n = '-' : makeHLine (n-1)

  render (VLine (px, py) s c) (Viewport (ox,oy) w h) =
      setFColor c >> makeVLine (ox+px-1,oy+py-1) s
      where
        makeVLine _ 0 = return ()
        makeVLine (px,py) n = writeAt (px,py) "|" >> makeVLine (px,py+1) (n-1)

  render (Rect (px, py) w h c) vp =
      render (HLine (px,py) w c) vp >>
      render (HLine (px,py+h-1) w c) vp >>
      render (VLine (px,py) h c) vp >>
      render (VLine (px+w-1,py) h c) vp

  render (Box (px, py) w h c) (Viewport (ox,oy) vw vh)  =
    setBColor c >>
    drawLines (ox+px-1, oy+py-1) w h >>
    setBColor BLACK
      where
        drawLines _ _ 0 = return ()
        drawLines (x,y) w' n = writeAt (x,y) spaces >> drawLines (x,y+1) w' (n-1)
        spaces = (makeSpaceLine w)
          where
            makeSpaceLine 0 = ""
            makeSpaceLine n = ' ' : makeSpaceLine (n-1)

  renderAll vp l = foldr (\shape act -> act >> (render shape vp)) (return ()) l

  renderWithClipping (Text p t c) vp =
    setFColor c >> writeAtWithClipping p t vp

  renderWithClipping (HLine p s c)  vp =
    setFColor c >> writeAtWithClipping p (makeHLine s) vp
      where
        makeHLine 0 = ""
        makeHLine n = '-' : makeHLine (n-1)

  renderWithClipping (VLine p s c) vp =
    setFColor c >> makeVLine p s
      where
        makeVLine _ 0 = return ()
        makeVLine (px,py) n = writeAtWithClipping (px,py) "|" vp >> makeVLine (px,py+1) (n-1)

  renderWithClipping (Rect (px, py) w h c) vp =
    renderWithClipping (HLine (px,py) w c) vp >>
    renderWithClipping (HLine (px,py+h-1) w c) vp >>
    renderWithClipping (VLine (px,py) h c) vp >>
    renderWithClipping (VLine (px+w-1,py) h c) vp

  renderWithClipping (Box p w h c) vp  =
    setBColor c >>
    drawLines p w h >>
    setBColor BLACK
      where
          drawLines _ _ 0 = return ()
          drawLines (x,y) w' n = writeAtWithClipping (x,y) spaces vp >> drawLines (x,y+1) w' (n-1)
          spaces = (makeSpaceLine w)
            where
              makeSpaceLine 0 = ""
              makeSpaceLine n = ' ' : makeSpaceLine (n-1)

  renderAllWithClipping vp l =
    foldr (\shape act -> act >> (renderWithClipping shape vp)) (return ()) l

instance Show TObj where
  show (Text p t c) = "(Text "++(show t)++" at pos "++(show p)++" color "++(show c)++")"
  show  (HLine p s c) = "(HLine at pos "++(show p)++" color "++(show c)++ ")"
  show  (VLine p s c) = "(VLine at pos "++(show p)++" color "++(show c)++ ")"
  show  (Rect p w h c) = "(Rect at pos "++(show p)++" width "++(show w)++" height "++(show h)++" color "++(show c)++ ")"
  show  (Box p w h c) = "(Box at pos "++(show p)++" width "++(show w)++" height "++(show h)++" color "++(show c)++ ")"

-- test elements

testvp1 = Viewport (5,5) 40 20
testvp2 = Viewport (5,5) 20 10
obj1 = HLine (2,4) 10 WHITE
obj2 = Text (4,2) "hello hello hello world " RED
obj3 = VLine (3,3) 15 GREEN
obj4 = Rect (6,1) 20 5 BLUE
obj5 = Box (6,6) 18 8 YELLOW
obj6 = Box (10,10) 8 2 BLUE
objl = [obj1, obj2, obj3, obj4, obj6, obj5]

test1 = cls >> renderAll testvp1 objl >> goto (1,20)
test2 = cls >> renderAllWithClipping testvp2 objl >> goto (1,20)
