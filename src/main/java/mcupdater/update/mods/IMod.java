package mcupdater.update.mods;

import mcupdater.update.IUpdatable;

public interface IMod extends IUpdatable {

    String getModID();

    String getName();

}
