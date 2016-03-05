package com.sockfullofpennies.epytome.world;

import java.util.LinkedList;
import java.util.List;

public class PlotLoc {
	public static final PlotLoc Directions[] =
	{
		new PlotLoc(-1,-1),
		new PlotLoc(-2,0),
		new PlotLoc(-1,1),
		new PlotLoc(1,1),
		new PlotLoc(2,0),
		new PlotLoc(1,-1),
	};
	
	public PlotLoc() {
		X=0; Y=0;
	}
	public PlotLoc(int x, int y) {
		X=x;Y=y;
	}
	public Integer X;
	public Integer Y;

	public PlotLoc add(PlotLoc loc) {
		return new PlotLoc(X+loc.X, Y+loc.Y);
	}
	
	public static List<PlotLoc> createRange(PlotLoc startPlot, PlotLoc endPlot) {
		List<PlotLoc> plotLocs = new LinkedList<PlotLoc>();
		for (int y=startPlot.Y; y<=endPlot.Y; y++) {
			int xoff = Math.abs(y%2);
			for (int x=startPlot.X+xoff; x<=endPlot.X+xoff; x+=2) {
				plotLocs.add(new PlotLoc(x,y));
			}
		}
		return plotLocs;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((X == null) ? 0 : X.hashCode());
		result = prime * result + ((Y == null) ? 0 : Y.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlotLoc other = (PlotLoc) obj;
		if (X == null) {
			if (other.X != null)
				return false;
		} else if (!X.equals(other.X))
			return false;
		if (Y == null) {
			if (other.Y != null)
				return false;
		} else if (!Y.equals(other.Y))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return X + "," + Y;
	}
}