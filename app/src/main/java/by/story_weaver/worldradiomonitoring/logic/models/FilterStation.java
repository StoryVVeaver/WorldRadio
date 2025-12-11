package by.story_weaver.worldradiomonitoring.logic.models;

import java.util.Objects;

public class FilterStation {
    private final String code;

    public FilterStation(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterStation)) return false;
        FilterStation that = (FilterStation) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
