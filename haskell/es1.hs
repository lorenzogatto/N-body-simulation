
import Data.List
import Data.Ord
data Elem = Dot | Square deriving (Eq)

maxSubSeq :: [Elem] -> Maybe Int
maxSubSeq xs = do
  let len = subSeqLen xs 
  if fst(maximumBy (comparing fst) (zip len [0..]))==0 
    then Nothing 
    else Just(snd(maximumBy (comparing fst) (zip len [0..])))


--per ogni elemento della lista, calcolo la lunghezza della sottosequenza di dot che parte da lì
subSeqLen xs = foldr (\x y -> if x == Dot then (head y + 1):y else 0:y) [0] xs


main = do
  print (maxSubSeq [Square, Square]) --Nothing
  print (maxSubSeq [Dot, Square, Square, Dot, Dot, Dot, Square, Dot, Dot]) --Just 3
  print (maxSubSeq []) --Nothing
  print (maxSubSeq [Dot, Dot, Dot, Square]) --Just 0

