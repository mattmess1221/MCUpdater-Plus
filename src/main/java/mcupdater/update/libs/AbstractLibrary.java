package mcupdater.update.libs;

import mcupdater.update.IUpdatable;

public abstract class AbstractLibrary implements ILibrary {

    protected String group;
    protected String name;
    protected String version;
    protected String classifier;

	@Override
	public boolean equals(IUpdatable updatable) {
		if(updatable == null || !(updatable instanceof ILibrary))
			return false;
		ILibrary library = (ILibrary) updatable;
        return library.getName().equals(this.getName())
                && library.getGroup().equals(this.getGroup())
                && library.getVersion().equals(this.getVersion());
    }

    public String getGroup(){
        return this.group;
    }

    public String getName(){
        return this.name;
    }

    public String getVersion(){
        return this.version;
    }

    public String getClassifier(){
        return this.classifier;
    }

}
