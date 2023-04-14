package com.example.smartsavr;

import java.util.Objects;

public class ProfilePictureItem {
    private final int resourceId;

    public ProfilePictureItem(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfilePictureItem that = (ProfilePictureItem) o;
        return resourceId == that.resourceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId);
    }
}
