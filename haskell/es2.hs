import qualified Data.Map as M (fromListWith,toList)
import qualified Data.List as L (sortBy)

--la funzione levelWithMaxSquare è quella finale

data Elem = Dot | Square deriving (Eq)
data BTree a = Empty | Node a (BTree a) (BTree a)

--countsquares prende in input (sotto)albero e livello di partenza. Da in output una lista (livello, 1) per ogni square trovato nel (sotto)albero
countSquares :: BTree Elem  -> Int -> [(Int,Int)]
countSquares Empty _ = []
countSquares (Node Square l r) n = (n,1) : (countSquares l (n+1)) ++ (countSquares r (n+1))
countSquares (Node Dot l r) n = (countSquares l (n+1)) ++ (countSquares r (n+1))


levelWithMaxSquare  :: BTree Elem  -> Maybe Int
levelWithMaxSquare  Empty = Nothing
levelWithMaxSquare  x = if countSquares x 0 == []
  then Nothing
  else Just $ fst $ head $ L.sortBy (\(k1,v1) (k2,v2) -> compare v2 v1) $ M.toList $ M.fromListWith (+) $ countSquares x 0

main = do
  let tree1 = (Node Dot Empty Empty)
  let tree2 = (Node Dot (Node Dot (Node Square Empty Empty) (Node Dot (Node Dot Empty Empty) (Node Dot Empty Empty))) (Node Square (Node Square Empty (Node Dot Empty Empty))Empty))
  let tree3 = Empty
  let tree4 = (Node Square (Node Square Empty Empty) (Node Square Empty Empty))
  print (levelWithMaxSquare tree1) -- Nothing
  print (levelWithMaxSquare tree2) -- Just 2
  print (levelWithMaxSquare tree3) -- Just 1
  print (levelWithMaxSquare tree4) -- Nothing
