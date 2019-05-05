package project.campusmap;

import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CampusSearchManager {

    public static List<Building> BUILDINGS = new ArrayList<>();

    public CampusSearchManager(){}

    static {
        BUILDINGS.add(new Building("Главный учебный корпус", new Point(60.007288, 30.372939)));
        BUILDINGS.add(new Building("Химический корпус", new Point(60.006546, 30.376382)));
        BUILDINGS.add(new Building("Механический корпус", new Point(60.008249, 30.375979)));
        BUILDINGS.add(new Building("Гидрокорпус-1", new Point(60.00565, 30.38176)));
        BUILDINGS.add(new Building("Гидрокорпус-2", new Point(60.00670, 30.38266)));
        BUILDINGS.add(new Building("НИК", new Point(60.006050, 30.379125)));
        BUILDINGS.add(new Building( "1-й учебный корпус", new Point(60.008806, 30.372889)));
        BUILDINGS.add(new Building("2-й учебный корпус", new Point(60.008452, 30.374733)));
        BUILDINGS.add(new Building("3-й учебный корпус", new Point(60.007222, 30.381708)));
        BUILDINGS.add(new Building("4-й учебный корпус", new Point(60.007305, 30.376825)));
        BUILDINGS.add(new Building("5-й учебный корпус", new Point(59.999584, 30.374844)));
        BUILDINGS.add(new Building("6-й учебный корпус", new Point(60.000055, 30.367645)));
        BUILDINGS.add(new Building("9-й учебный корпус", new Point(60.000771, 30.366481)));
        BUILDINGS.add(new Building("10-й учебный корпус", new Point(60.000595, 30.369683)));
        BUILDINGS.add(new Building("11-й учебный корпус", new Point(60.009320, 30.378444)));
        BUILDINGS.add(new Building("15-й учебный корпус", new Point(60.008452, 30.374733)));
        BUILDINGS.add(new Building("16-й учебный корпус", new Point(60.007223, 30.390456)));
        BUILDINGS.add(new Building("ИМОП", new Point(60.007223, 30.390456)));
        BUILDINGS.add(new Building("Спортивный комплекс", new Point(60.002845, 30.368710)));
        BUILDINGS.add(new Building("Лабораторный корпус", new Point(60.007405, 30.379875)));
        BUILDINGS.add(new Building("Гидробашня", new Point(60.005783, 30.374270)));
        BUILDINGS.add(new Building("НОЦ РАН", new Point(60.002887, 30.373448)));
        BUILDINGS.add(new Building("1-й профессорский корпус", new Point(60.004989, 30.369800)));
        BUILDINGS.add(new Building("2-й профессорский корпус", new Point(60.004864, 30.378097)));
        BUILDINGS.add(new Building("Дом ученых в Лесном", new Point(60.004338, 30.380082)));
        BUILDINGS.add(new Building("Секретариат приемной комиссии", new Point(60.009405, 30.371689)));
        BUILDINGS.add(new Building("Институт промышленного менеджмента, экономики и торговли", new Point(59.994793, 30.357806)));
        BUILDINGS.add(new Building("Центр профориентации и довузовской подготовки", new Point(60.009432, 30.371535)));
        BUILDINGS.add(new Building("Управление Студенческого городка", new Point(59.998830, 30.374299)));
        BUILDINGS.add(new Building("Общежитие 1", new Point(59.986116, 30.342260)));
        BUILDINGS.add(new Building("Общежитие 3", new Point(59.986653, 30.343390)));
        BUILDINGS.add(new Building("Общежитие 4", new Point(59.986405, 30.345824)));
        BUILDINGS.add(new Building("Общежитие 5", new Point(59.986383, 30.347670)));
        BUILDINGS.add(new Building("Общежитие 6", new Point(59.986726, 30.348074)));
        BUILDINGS.add(new Building("Общежитие 7", new Point(59.986714, 30.342709)));
        BUILDINGS.add(new Building("Общежитие 8", new Point(59.999473, 30.372145)));
        BUILDINGS.add(new Building("Общежитие 10", new Point(59.998433, 30.370655)));
        BUILDINGS.add(new Building("Общежитие 11", new Point(59.985596, 30.344902)));
        BUILDINGS.add(new Building("Общежитие 12", new Point(59.998860, 30.375693)));
        BUILDINGS.add(new Building("Общежитие 13", new Point(60.008519, 30.391859)));
        BUILDINGS.add(new Building("Общежитие 14", new Point(59.998830, 30.374299)));
        BUILDINGS.add(new Building("Общежитие 15", new Point(60.007324, 30.389398)));
        BUILDINGS.add(new Building("Общежитие 16", new Point(60.047569, 30.334468)));
        BUILDINGS.add(new Building("Общежитие 17", new Point(60.021809, 30.388174)));
        BUILDINGS.add(new Building("Общежитие 18", new Point(60.021907, 30.387209)));
        BUILDINGS.add(new Building("Общежитие 19", new Point(59.859080, 30.327025)));
        BUILDINGS.add(new Building("Общежитие 20", new Point(59.911656, 30.319937)));
        BUILDINGS.add(new Building("Студенческий клуб", new Point(59.986394, 30.346538)));
    }

    public static List<Building> searchByName(String searchText) {
        Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
        List<Building> result = new ArrayList<>();
        for (Building building : BUILDINGS) {
            Matcher matcher = pattern.matcher(building.name);
            if (matcher.find()) {
                result.add(building);
            }
        }
        return result;
    }

    public static Building searchExactlyByName(String searchText) {
        if (searchText.length() < 3) return null;
        Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE | Pattern.LITERAL);
        List<Building> result = new ArrayList<>();
        for (Building building : BUILDINGS) {
            Matcher matcher = pattern.matcher(building.name);
            if (matcher.find()) {
                result.add(building);
            }
        }
        if (result.size() > 1) return null; else return result.get(0);
    }
}