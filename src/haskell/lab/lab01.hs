{-
  PAP 2015-2016
  Lab 01
-}

-- countElems

countElems :: [Int] -> Int -> Int
countElems [] _ = 0
countElems (x:xs) v
  | x == v = 1 + rest
  | otherwise = rest
  where
      rest = (countElems xs v)

-- countElemsWithPred

countElemsWithPred :: [Int] -> (Int -> Bool) -> Int
countElemsWithPred [] _ = 0
countElemsWithPred (x:xs) p
  | p x = 1 + (countElemsWithPred xs p)
  | otherwise = countElemsWithPred xs p

-- countDots

data Elem = Dot | Star

countDots :: [Elem] -> Int
countDots [] = 0
countDots (Dot:xs) = 1 + countDots xs
countDots (_:xs) = countDots xs

-- rev

rev :: [a] -> [a]
rev [] = []
rev (x:xs) = (rev xs) ++ [x]

--removeAll :: [a] -> a -> [a]

removeAll [] _ = []
removeAll (x:xs) v
  | x == v = removeAll xs v
  | otherwise = x : (removeAll xs v)

-- isPresentInBST

data BSTree = Nil | Node Int BSTree BSTree

isPresentInBST :: Int -> BSTree -> Bool
isPresentInBST _ Nil = False
isPresentInBST v (Node x l r)
  | x == v = True
  | x < v = isPresentInBST v r
  | otherwise = isPresentInBST v l

-- insertInBST

insertInBST :: String -> BSTree -> BSTree
insertInBST s Nil = (Node s Nil Nil)
insertInBST s (Node v l r)
  | s <= v = (Node v (insertInBST s l) r)
  | otherwise = (Node v l (insertInBST s r))

-- buildBST

buildBST :: [String] -> BSTree
buildBST [] = Nil
buildBST (x:xs) = insertInBST x (buildBST xs)

tt :: BSTree
tt = (Node 10
      (Node 5
        (Node 1 Nil Nil)
        Nil)
      (Node 13
        (Node 11 Nil Nil)
        Nil))
