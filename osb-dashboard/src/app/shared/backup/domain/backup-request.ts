import { FileEndpoint } from './file-endpoint';
import { DbEndpoint } from './db-endpoint';

export interface BackupRequest {

    source: DbEndpoint;

    destination: FileEndpoint;

}
