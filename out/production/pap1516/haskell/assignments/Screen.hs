module Screen  where

type Pos = (Int,Int)

data Color =
		BLACK | RED | GREEN | YELLOW |
		BLUE | MAGENTA | CYAN | WHITE

data Viewport = Viewport Pos Int Int

-- clear the text screen
cls :: IO()
cls = putStr "\ESC[2J"

-- make a beep
beep :: IO()
beep = putStr "\BEL"

-- move the cursor at the new pos (x,y)
goto :: Pos -> IO ()
goto (x,y) =
	putStr("\ESC["++show y++";"++show x++"H")

-- write at the cursor
writeAt :: Pos -> String -> IO()
writeAt p s = goto p >> putStr s

--set foreground color
setFColor :: Color -> IO()
setFColor c = putStr ("\ESC["++showFColor c++"m")
	where
		showFColor BLACK = "30"
		showFColor RED = "31"
		showFColor GREEN = "32"
		showFColor YELLOW = "33"
		showFColor BLUE = "34"
		showFColor MAGENTA = "35"
		showFColor CYAN = "36"
		showFColor WHITE = "37"

--set background color
setBColor :: Color -> IO()
setBColor c = putStr ("\ESC["++showBColor c++"m")
	where
		showBColor BLACK = "40"
		showBColor RED = "41"
		showBColor GREEN = "42"
		showBColor YELLOW = "43"
		showBColor BLUE = "44"
		showBColor MAGENTA = "45"
		showBColor CYAN = "46"
		showBColor WHITE = "47"
