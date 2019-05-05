package project.campusmap;

import com.yandex.mapkit.geometry.Point;

import javax.sql.StatementEvent;

public class Building {

    String name;

    Point location;

    public Building(String name, Point location) {
        this.name = name;
        this.location = location;
    }
}
