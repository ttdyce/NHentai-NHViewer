package personal.ttd.nhviewer.Saver;

public class SaverMaker {
    public static Saver getDefaultSaver(){
        return getJSONSaver();
    }

    private static Saver getJSONSaver(){
        return new JSONSaver();
    }
    private static Saver getDBSaver(){
        return new DBSaver();
    }
}
