import { FileEndpoint } from './file-endpoint';
import { DbEndpoint } from './db-endpoint';
export interface BackupPlan {

    // Do not provide during creation
    id: string;

    frequency: string;

    retentionStyle: string;

    // Either number of files, or hours, or days or whatever
    retentionPeriod: number;

    destination: FileEndpoint;

    source: DbEndpoint;

}
