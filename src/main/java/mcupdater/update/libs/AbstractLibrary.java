package mcupdater.update.libs;

import mcupdater.update.IUpdatable;

public abstract class AbstractLibrary implements ILibrary {

    public boolean equals(IUpdatable updatable) {
        if (updatable == null || !(updatable instanceof ILibrary))
            return false;
        ILibrary library = (ILibrary) updatable;
        if (library.getName().equals(this.getName()) && library.getGroup().equals(this.getGroup())
                && library.getVersion().equals(this.getVersion())) {
            return true;
        }
        return false;
    }

}
