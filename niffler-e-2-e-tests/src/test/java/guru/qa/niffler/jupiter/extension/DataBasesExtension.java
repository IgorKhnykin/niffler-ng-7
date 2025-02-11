package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.tpl.Connections;

public class DataBasesExtension implements SuiteExtension {

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
        EntityManagers.closeAllEmfs();
    }
}
