MACHINE DefSetTest1
SETS DS 
VARIABLES x, y, z
INVARIANT x<2 & y:{0,1} & z:{0,1}
INITIALISATION x:=0 || y:= 0 || z:=0
OPERATIONS
 inc1(a,b) = SELECT a:DS & b:DS & a/=b & y=0 THEN x:=x+1 || y := 1 END;
 inc2(a) = SELECT a:DS & z=0 THEN x:=x+1 || z := 1 END
END
