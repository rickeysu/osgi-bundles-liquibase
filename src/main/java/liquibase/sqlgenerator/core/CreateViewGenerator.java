package liquibase.sqlgenerator.core;

import liquibase.database.Database;
import liquibase.database.core.*;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGenerator;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.CreateViewStatement;

public class CreateViewGenerator implements SqlGenerator<CreateViewStatement> {
    public int getPriority() {
        return PRIORITY_DEFAULT;
    }

    public boolean supports(CreateViewStatement statement, Database database) {
        return true;
    }

    public ValidationErrors validate(CreateViewStatement createViewStatement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors validationErrors = new ValidationErrors();

        validationErrors.checkRequiredField("viewName", createViewStatement.getViewName());
        validationErrors.checkRequiredField("selectQuery", createViewStatement.getSelectQuery());

        if (database instanceof HsqlDatabase
                 || database  instanceof H2Database
                || database instanceof DB2Database
                || database instanceof CacheDatabase
                || database instanceof MSSQLDatabase
                || database instanceof DerbyDatabase
                || database instanceof SybaseASADatabase
                || database instanceof InformixDatabase) {
            if (createViewStatement.isReplaceIfExists()) {
                validationErrors.checkDisallowedField("replaceIfExists", createViewStatement.isReplaceIfExists());
            }
        }

        return validationErrors;
    }

    public Sql[] generateSql(CreateViewStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        String createClause;

        if (database instanceof FirebirdDatabase) {
            if (statement.isReplaceIfExists()) {
                createClause = "RECREATE VIEW";
            } else {
                createClause = "RECREATE VIEW";
            }
        } else if (database instanceof SybaseASADatabase && statement.getSelectQuery().toLowerCase().startsWith("create view")) {
            // Sybase ASA saves view definitions with header.
            return new Sql[]{
                    new UnparsedSql(statement.getSelectQuery())
            };
        } else {
            createClause = "CREATE " + (statement.isReplaceIfExists() ? "OR REPLACE " : "") + "VIEW";
        }

        return new Sql[]{
                new UnparsedSql(createClause + " " + database.escapeViewName(statement.getSchemaName(), statement.getViewName()) + " AS " + statement.getSelectQuery())
        };
    }
}
