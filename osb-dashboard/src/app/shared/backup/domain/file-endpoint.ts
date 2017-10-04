export interface FileEndpoint {

    authUrl: string;

    username: string;

    password: string;

    domain: string;

    projectName: string;

    // Type is always SWIFT for now
    type: string;

    containerName: string;

    id: string;

    // Do not provide when creating a request
    filename: string;

}
