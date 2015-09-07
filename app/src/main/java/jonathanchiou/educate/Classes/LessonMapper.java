package jonathanchiou.educate.Classes;

import java.util.Hashtable;
import java.util.Map;

import jonathanchiou.educate.Algebra1_Lessons.Algebra_1_Balancing_Equations;
import jonathanchiou.educate.Algebra1_Lessons.Algebra_1_EWZOIS;
import jonathanchiou.educate.Algebra1_Lessons.Algebra_1_Equation_With_Variables;
import jonathanchiou.educate.Algebra1_Lessons.Algebra_1_Inequalities;
import jonathanchiou.educate.Algebra1_Lessons.Algebra_1_LEAWP;
import jonathanchiou.educate.Algebra1_Lessons.Algebra_1_PEMDAS;
import jonathanchiou.educate.Algebra1_Lessons.Algebra_1_System_Of_Equations;
import jonathanchiou.educate.Algebra1_Lessons.Algebra_1_Variables;
import jonathanchiou.educate.havingtrouble;

/**
 * Created by jman0_000 on 9/7/2015.
 */
public class LessonMapper {

    private static LessonMapper instance = null;
    private static Map<String, Class> activity_map = new Hashtable<String, Class>();

    private LessonMapper() {}

    public static LessonMapper getInstance() {
        if (instance == null)
                instance = new LessonMapper();

        return instance;
    }

    public static void setUpMap() {
        activity_map.put("Variables", Algebra_1_Variables.class);
        activity_map.put("PEMDAS", Algebra_1_PEMDAS.class);
        activity_map.put("Equations with Variables",  Algebra_1_Equation_With_Variables.class);
        activity_map.put("Balancing Equations", Algebra_1_Balancing_Equations.class);
        activity_map.put("Equations with Zero or Infinite Solutions", Algebra_1_EWZOIS.class);
        activity_map.put("System of Equations", Algebra_1_System_Of_Equations.class);
        activity_map.put("Linear Equations and Word Problems", Algebra_1_LEAWP.class);
        activity_map.put("Inequalities", Algebra_1_Inequalities.class);
        activity_map.put("Getting Answers", havingtrouble.class);
    }

    public Class getLesson(String lesson) {
        return activity_map.get(lesson);
    }

}


