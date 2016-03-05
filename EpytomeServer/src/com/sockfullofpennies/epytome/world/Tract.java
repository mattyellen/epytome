package com.sockfullofpennies.epytome.world;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.sockfullofpennies.epytome.db.EpytomeDB.TractModel;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.util.Persistent;

public class Tract extends Persistent<TractModel>{
	
	private static final PlotLoc TRACT_SIZE = new PlotLoc((World.VIEW_DIST.X+1)*2, (World.VIEW_DIST.Y+1)*2);
	private boolean tractExists;
	private Long worldId;
	private String tractId;
	private PlotLoc tractLoc;
	
	public Tract(Long wid, int x, int y) {
		super(TractModel.class);
		worldId = wid;
		tractId = makeId(wid, x, y);
		tractLoc = getTractForPlot(x,y);
		tractExists = load(getKey(wid, x, y));
	}
	
	public static String makeId(Long wid, int x, int y) {
		PlotLoc tractLoc = getTractForPlot(x,y);
		return "w:"+wid+"|t:"+tractLoc.X+","+tractLoc.Y;
	}
	
	public static Key<TractModel> getKey(Long wid, int x, int y) {
		return Key.create(TractModel.class, makeId(wid, x, y));
	}

	public void create() throws CommandFailedException {
		if (tractExists) {
			//tract already exists in this world...  we're done
			return;
		}

		ofy().transact(new VoidWork() {
			public void vrun() {
				// In the transaction...  check again in case someone beat us to this.
				if (reload()) {
					//Someone beat us to it.
					return;
				}
				
				model.Id=tractId;
				
				// Do the real work of generating plots
				PlotLoc startPlot = new PlotLoc();
				PlotLoc endPlot = new PlotLoc();
				getPlotRangeForTract(tractLoc.X, tractLoc.Y, startPlot, endPlot);
				
				//Get the initial list of plot locations
				List<PlotLoc> plotLocs = PlotLoc.createRange(startPlot, endPlot);
				
				Map<String, Double> heightMap = generateHeightMap(plotLocs);
				for(PlotLoc loc : plotLocs) {
					Plot.Type type;
					double probVal = heightMap.get(loc.toString());
					if (probVal > 0.5) {
						type = Plot.Type.Mountain;
					}
					else if (probVal < -0.5){
						type = Plot.Type.Water;
					}
					else {
						type = Plot.Type.Grass;
					}
					
					new Plot(worldId, key, loc.X, loc.Y, type);
					System.out.println("Create Plot: ("+loc+"): "+type);
				}

				//Generate quarries
				for(PlotLoc loc : plotLocs) {
					double rand = Math.random();
					if (rand > 0.05) continue;
					
					Plot plot = new Plot(worldId, loc);
					if (plot.getType() != Plot.Type.Grass) continue;
					
					int mountainCount = 0;
					int grassCount = 0;
					for(PlotLoc dir : PlotLoc.Directions) {
						Plot adjPlot = new Plot(worldId, loc.add(dir));
						if (adjPlot.getType() == Plot.Type.Mountain) {
							mountainCount++;
						}
						else if (adjPlot.getType() == Plot.Type.Grass) {
							grassCount++;
						}
					}
					
					if (mountainCount >= 2 && grassCount >= 1) {
						plot.setConstruction(Construction.Type.Quarry);
						new ResourceConstruction(plot.getId(), 10);
					}	
				}
				
				save();
			}
		});
	}

	private static PlotLoc getTractForPlot(int x, int y) {
		int tractX = (int)Math.floor((double)x/(TRACT_SIZE.X*2));
		int tractY = (int)Math.floor((double)y/(TRACT_SIZE.Y));
		return new PlotLoc(tractX, tractY);
	}

	private void getPlotRangeForTract(int x, int y, PlotLoc startLoc, PlotLoc endLoc) {
		startLoc.X = x*(TRACT_SIZE.X*2);
		startLoc.Y = y*(TRACT_SIZE.Y);
		
		endLoc.X = startLoc.X+(TRACT_SIZE.X-1)*2;
		endLoc.Y = startLoc.Y+(TRACT_SIZE.Y-1);
	}
	
	private Map<String, Double> generateHeightMap(List<PlotLoc> plotLocs) {
		//Seed the height map with random bumps and depressions
		Map<String, Double> heightMap = new HashMap<String, Double>();
		for(PlotLoc loc : plotLocs) {
			String key = loc.toString();
			double val;
			double rand = Math.random();
			if (rand < 0.05) {
				val = 0.2;
			}
			else if (rand < 0.1) {
				val = -0.2;
			}
			else {
				val = 0;
			}
			heightMap.put(key, val);
			//System.out.println("Initial Map: "+loc+": "+val);
		}
		
		//Multiple passes, make the tall stuff taller and spread out
		//Make the short stuff shorter
		for(int reps=0; reps<4; reps++) {
			Map<String, Double> newMap = new HashMap<String, Double>();
			for(PlotLoc loc : plotLocs) {
				String key = loc.toString();
				double val = heightMap.get(key);
				if (val != 0) {
					val += (val/Math.abs(val)) * 0.2;
					
					for(PlotLoc dir : PlotLoc.Directions) {
						PlotLoc adjLoc = loc.add(dir);
						String adjKey = adjLoc.toString();
						if (heightMap.containsKey(adjKey)) {
							double adjVal = heightMap.get(adjKey);
							if (adjVal == 0) {
									adjVal = (val/Math.abs(val)) * 0.2;
									newMap.put(adjKey, adjVal);
							}
						}
					}
				}
				newMap.put(key, val);

				//System.out.println("Pass "+reps+" Map: "+loc+": "+val);
			}
			heightMap = newMap;
		}
		return heightMap;
	}
}
