package de.bmoth.parser.ast.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDefinedElementType implements BType {

    private final String setName;
    private final List<String> elements;

    public UserDefinedElementType(String name, List<String> list) {
        this.setName = name;
        this.elements = list;
    }

    public String getSetName() {
        return this.setName;
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (!this.unifiable(otherType)) {
            throw new UnificationException();
        }
        if (otherType instanceof UntypedType) {
            ((UntypedType) otherType).replaceBy(this);
        }
        return this;
    }

    @Override
    public boolean unifiable(BType otherType) {
        if (otherType instanceof UntypedType) {
            return true;
        }
        return otherType == this;
    }

    @Override
    public boolean contains(BType other) {
        return false;
    }

    @Override
    public boolean isUntyped() {
        return false;
    }

    @Override
    public String toString() {
        return this.setName;
    }

    public List<String> getElements() {
        if (this.elements != null) {
            return new ArrayList<>(this.elements);
        }
        return Collections.emptyList();
    }

}
