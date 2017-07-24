{-
  PAP 2015-2016
  Assignment #3 - Soluzioni
  v 1.0 
-}

data BSTree a = Nil | Node a (BSTree a) (BSTree a)
      deriving Show

-- bstMap
bstMap ::  Ord a => (a -> b) -> BSTree a -> BSTree b
bstMap _ Nil = Nil
bstMap f (Node el l r) = Node (f el) (bstMap f l) (bstMap f r)

-- bstFold
bstFold ::  Ord a => (b -> a -> b) -> b -> BSTree a -> b
bstFold _ init Nil = init
bstFold f init (Node el l r) = bstFold f (bstFold f (f init el) l) r 

-- bstFilter
bstFilter :: (a -> Bool) -> BSTree a -> BSTree a
bstFilter _ Nil = Nil
bstFilter f (Node el l r) 
  | f el = Node el (bstFilter f l) (bstFilter f r)
  | otherwise = attach (bstFilter f l) (bstFilter f r) 
  where 
    attach Nil t = t
    attach (Node el l r) t = Node el l (attach r t) 

-- variant using lists
bstFilter' :: Ord a => (a -> Bool) -> BSTree a -> BSTree a
bstFilter' p = listToBst . filter p . bstToList 
  where
    bstToList t = bstFold (\l e -> e : l) [] t
    listToBst l = foldl (\t a -> insertInBST a t) Nil l

insertInBST :: Ord a => a -> BSTree a -> BSTree a
insertInBST s Nil = (Node s Nil Nil)
insertInBST s (Node v l r)
  | s <= v = (Node v (insertInBST s l) r)
  | otherwise = (Node v l (insertInBST s r))

-- bstForEach
bstForEach ::  Ord a => (a -> IO ()) -> BSTree a -> IO ()
bstForEach _ Nil = return ()
bstForEach f (Node el l r) = (bstForEach f l) >> (f el) >> (bstForEach f r)

--

type Age = Int
data Person = Person String Age

instance Show Person where
  show (Person n a) = "{ name: " ++ n ++ ", age: " ++ (show a) ++ " }"

instance Eq Person where
  (Person n _) == (Person n' _) = n == n'
  (Person n _) /= (Person n' _) = n /= n'

instance Ord Person where
  (Person n _) < (Person n' _) = n < n'
  (Person n _) <= (Person n' _) = n <= n'
  (Person n _) > (Person n' _) = n > n'
  (Person n _) >= (Person n' _) = n >= n'

-- printPers
printPers :: BSTree Person -> Int -> Int -> IO()
printPers t min max = 
  (bstForEach (\(Person n _) -> putStrLn n) 
    . bstFilter (\(Person _ a) -> a >= min && a <= max)) t 

-- testt examples

testTree = 
  Node 10 
    ( Node 5 
      Nil
      ( Node 7 Nil Nil ) )
    ( Node 15
      ( Node 12 Nil Nil ) 
      ( Node 18 Nil Nil ) )

testMap = bstMap (\x -> x + 1) testTree 

testFold = bstFold (\x y -> x + y) 0 testTree

testFilter = bstFilter (\x -> x < 11) testTree

testForEach = bstForEach (\x -> putStrLn (show x)) testTree

testTree2 = 
  (insertInBST (Person "anna" 25)
  . insertInBST (Person "giulio" 18)
  . insertInBST (Person "alda" 80)
  . insertInBST (Person "bianca" 20)
  . insertInBST (Person "marco" 35)) Nil

testPrintPerson = printPers testTree2 20 30

