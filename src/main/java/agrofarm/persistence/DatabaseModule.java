package agrofarm.persistence;

import agrofarm.TechOperationStore;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TechOperationStore.class).to(SQLiteTechOperationStore.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    Connection provideConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:target/agrofarm.db");
    }
}
