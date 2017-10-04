package de.evoila.cf.broker.service;

import de.evoila.cf.model.enums.DatabaseType;

public interface BackupTypeService {
    DatabaseType getType ();
}
