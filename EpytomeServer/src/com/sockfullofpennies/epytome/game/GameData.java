package com.sockfullofpennies.epytome.game;

import java.util.HashMap;
import java.util.Map;

import com.sockfullofpennies.epytome.world.Construction;
import com.sockfullofpennies.epytome.world.Construction.Type;

public class GameData {
	public static final Map<Construction.Type, ConstructionParameters> Construction;
	
	static
	{
		Construction = new HashMap<Construction.Type, ConstructionParameters>();
		Construction.put(Type.None, new ConstructionParameters()
		{{
			MoveCost = 10;
			UpgradeType = Type.None;
		}});
		Construction.put(Type.Road, new ConstructionParameters()
		{{
			MoveCost = 5;
			UpgradeType = Type.StoneRoad;
		}});
		Construction.put(Type.StoneRoad, new ConstructionParameters()
		{{
			MoveCost = 1;
			UpgradeType = Type.None;
		}});
		Construction.put(Type.Quarry, new ConstructionParameters()
		{{
			MoveCost = 5;
			UpgradeType = Type.None;
		}});
		
	}
}
