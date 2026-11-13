package agrofarm.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class FieldIdentityModule extends AbstractModule {
    private final String fieldId;
    private final String fieldName;

    public FieldIdentityModule(String fieldId, String fieldName) {
        if (fieldId == null || fieldId.isBlank()) {
            throw new IllegalArgumentException("Ідентифікатор поля не може бути порожнім.");
        }
        if (fieldName == null || fieldName.isBlank()) {
            throw new IllegalArgumentException("Назва поля не може бути порожньою.");
        }
        this.fieldId = fieldId;
        this.fieldName = fieldName;
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("fieldId")).toInstance(fieldId);
        bind(String.class).annotatedWith(Names.named("fieldName")).toInstance(fieldName);
    }
}
