package listener;

/**
 * Created by biresh.singh on 16-06-2018.
 */

public interface NavigationDeleget {
    public void executeFragment(String fragmentName, Object obj);
    public void goBack();
}

