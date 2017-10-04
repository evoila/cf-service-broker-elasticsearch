import { FileEndpoint } from './file-endpoint';
export interface Job {

    id: string;

    startDate: number;

    instanceId: string;

    // Possible values are STARTED, IN PROGRESS, SUCCESS and FAILED.
    status: string;

    jobType: string;

    // Optional, only exists, if Backup Job succeeded
    destination: FileEndpoint;

    logs: string[];

}
