printVariance :: [Float] -> IO()

--data una lista di numeri reali, stampa in uscita gli scarti dei valori dalla media, uno per linea, considerando solo gli elementi della lista di valore maggiore al valore della media stessa. 

printVariance [] = return ()
printVariance xs = do
  let media = sum(xs) / fromIntegral (length xs)
  let xs2 = map (\x -> x-media) xs
  let xs3 = filter (\x -> x>0) xs2
  mapM_ print xs3

main = do
  printVariance [5.0, 7.0, 6.0, 9.0, 1.0, 2.0] --2, 1, 4
  printVariance [] -- print nothing
  printVariance [2.0] -- print nothing
  printVariance [1.0, 2.0] --0.5