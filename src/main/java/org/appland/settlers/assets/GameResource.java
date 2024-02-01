package org.appland.settlers.assets;

import java.util.Optional;

public abstract class GameResource {
    Optional<String> name = Optional.empty();

    public abstract GameResourceType getType();

    public void setName(String name) {
        this.name = Optional.of(name);
    }

    boolean isNameSet() {
        return name.isPresent();
    }

    String getName() {
        return name.get();
    }
}
