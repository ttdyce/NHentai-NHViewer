package github.ttdyce.nhviewer.Model.Saver;

public class SaverMaker {
    private static JSONSaver JSONSaver = new JSONSaver();
    private static DBSaver DBSaver= new DBSaver();

    public static Saver getDefaultSaver(){
        return JSONSaver;
    }

    public static JSONSaver getJSONSaver(){
        return JSONSaver;
    }
    public static DBSaver getDBSaver(){
        return DBSaver;
    }
}
