package liquibase.sqlgenerator.core;

import liquibase.database.Database;
import liquibase.database.core.InformixDatabase;
import liquibase.database.core.SQLiteDatabase;
import liquibase.database.core.OracleDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGenerator;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.AddForeignKeyConstraintStatement;

public class AddForeignKeyConstraintGenerator implements SqlGenerator<AddForeignKeyConstraintStatement> {
    public int getPriority() {
        return PRIORITY_DEFAULT;
    }

    public boolean supports(AddForeignKeyConstraintStatement statement, Database database) {
        return (!(database instanceof SQLiteDatabase));
    }

    public ValidationErrors validate(AddForeignKeyConstraintStatement addForeignKeyConstraintStatement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors validationErrors = new ValidationErrors();

        if ((addForeignKeyConstraintStatement.isInitiallyDeferred() || addForeignKeyConstraintStatement.isDeferrable()) && !database.supportsInitiallyDeferrableColumns()) {
            validationErrors.checkDisallowedField("initiallyDeferred", addForeignKeyConstraintStatement.isInitiallyDeferred(), database);
            validationErrors.checkDisallowedField("deferrable", addForeignKeyConstraintStatement.isDeferrable(), database);
        }

        validationErrors.checkRequiredField("baseColumnNames", addForeignKeyConstraintStatement.getBaseColumnNames());
        validationErrors.checkRequiredField("baseTableNames", addForeignKeyConstraintStatement.getBaseTableName());
        validationErrors.checkRequiredField("referencedColumnNames", addForeignKeyConstraintStatement.getReferencedColumnNames());
        validationErrors.checkRequiredField("referencedTableName", addForeignKeyConstraintStatement.getReferencedTableName());

        return validationErrors;
    }

    public Sql[] generateSql(AddForeignKeyConstraintStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
	    // If database doesn't support FK referenced on unique columns - skip FK statement generation
	    if (!statement.isReferencedToPrimary() && !(database instanceof OracleDatabase)) {
		    return new Sql[0];
	    }

	    StringBuilder sb = new StringBuilder();
	    sb.append("ALTER TABLE ")
			    .append(database.escapeTableName(statement.getBaseTableSchemaName(), statement.getBaseTableName()))
			    .append(" ADD CONSTRAINT ");
	    if (!(database instanceof InformixDatabase)) {
		    sb.append(database.escapeConstraintName(statement.getConstraintName()));
	    }
	    sb.append(" FOREIGN KEY (")
			    .append(database.escapeColumnNameList(statement.getBaseColumnNames()))
			    .append(") REFERENCES ")
			    .append(database.escapeTableName(statement.getReferencedTableSchemaName(), statement.getReferencedTableName()))
			    .append("(")
			    .append(database.escapeColumnNameList(statement.getReferencedColumnNames()))
			    .append(")");

	    if (statement.getOnUpdate() != null) {
		    if ((database instanceof OracleDatabase) && statement.getOnUpdate().equalsIgnoreCase("RESTRICT")) {
			    //don't use
		    } else {
			    sb.append(" ON UPDATE ").append(statement.getOnUpdate());
		    }
	    }

	    if (statement.getOnDelete() != null) {
		    if ((database instanceof OracleDatabase) && (statement.getOnDelete().equalsIgnoreCase("RESTRICT") || statement.getOnDelete().equalsIgnoreCase("NO ACTION"))) {
			    //don't use
		    } else {
			    sb.append(" ON DELETE ").append(statement.getOnDelete());
		    }
	    }

	    if (statement.isDeferrable() || statement.isInitiallyDeferred()) {
		    if (statement.isDeferrable()) {
			    sb.append(" DEFERRABLE");
		    }

		    if (statement.isInitiallyDeferred()) {
			    sb.append(" INITIALLY DEFERRED");
		    }
	    }

	    if (database instanceof InformixDatabase) {
		    sb.append(" CONSTRAINT ");
		    sb.append(database.escapeConstraintName(statement.getConstraintName()));
	    }

	    return new Sql[]{
			    new UnparsedSql(sb.toString())
	    };
    }
}
