import { DbEndpoint } from './db-endpoint';
import { FileEndpoint } from './file-endpoint';

export interface RestoreRequest {

    source: FileEndpoint;

    destination: DbEndpoint;

}
