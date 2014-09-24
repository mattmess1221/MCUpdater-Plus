package mcupdater.update.mods;

import com.google.gson.Gson;

import mcupdater.update.IUpdatable;

public abstract class AbstractMod implements IMod {

    protected String modid;

    protected Gson gson = new Gson();

    @Override
    public String getModID() {
        return this.modid;
    }

    @Override
    public boolean equals(IUpdatable updatable) {
        // TODO Auto-generated method stub
        return false;
    }
}
