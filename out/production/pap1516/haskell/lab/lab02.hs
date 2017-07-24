-- PAP a.a. 2015-2016 - Lab #02

-- Ex. 1 - mapLen

-- mapLen (con uso non di length ma di una versione foldr-based)
mapLen:: [String] -> [(String,Int)]
mapLen l = map (\w -> (w, len w)) l
  where
    len w = foldr (\_ c -> c + 1) 0 w

-- mapLen con list comprehension
mapLen':: [String] -> [(String,Int)]
mapLen' l = [ (w,length w) | w <- l]

-- Ex. 2 - selectedLen

selectedLen :: [String] -> Int -> [(String,Int)]
selectedLen l w = filter (\(_,l') -> l' > w) (mapLen l)

-- versione con list comprehension
selectedLen' :: [String] -> Int -> [(String,Int)]
selectedLen' l w = [ (w',length w') | w' <- l, length w' > w ]

-- versione come composizione di funzioni
selectedLen'' :: Int -> ([String] -> [(String,Int)])
selectedLen'' w = (filter (\(_,l') -> l' > w) . mapLen)

-- Ex. 3

wordOcc :: [String] -> String -> Int
wordOcc l w = foldr (\w' c -> if (w' == w) then c + 1 else c) 0 l

-- Ex. 4

member _ [] = False
member v (x:xs)
  | v == x = True
  | otherwise = member v xs

wordsOcc :: [String] -> [(Int, [String])]
wordsOcc l = (sortByOcc . myfold . getOccs) l
  where
      getOccs l =  map (\w -> (wordOcc l w, w)) l
      myfold l = foldr (\occ l' -> updateList occ l') [] l
        where
          updateList (o, w) [] = [(o, [w])]
          updateList (o, w) ((o',lw):xs)
            | o == o' && not (member w lw) = (o, w:lw) : xs
            | o == o' = (o, lw) : xs
            | otherwise = (o',lw) : updateList (o,w) xs
      sortByOcc [] = []
      sortByOcc ((o,l):xs) = sortByOcc le ++ [(o,l)] ++ sortByOcc ri
        where
          le = [ (o',l') | (o',l') <- xs, o' < o]
          ri = [ (o'',l'') | (o'',l'') <- xs, o'' >= o]
  
-- Ex. 5

printElems :: [String] -> IO()
printElems l = foldr (\s full -> putStrLn ("    " ++ s) >> full) (return ()) l
