export interface Configuration {
    appId: string;
    appName: string;
    creationTimeAsString: Date;
    creationTime: number;
    scaling: Scaling;
    cpu: Cpu;
    ram: Ram;
    latency: Latency;
    requests: Requests;
    learning: Learning;
}

export interface Scaling {
  scalingEnabled: boolean;
  predictionScalingEnabled: boolean;
  billingIntervalEnabled: boolean;
  scalingIntervalMultiplier: number;
  currentIntervalState: number;
  minInstances: number;
  maxInstances: number;
  cooldownTime: number;
}

export interface Cpu {
  upperLimit: number;
  lowerLimit: number;
  thresholdPolicy: string;
  cpuScalingEnabled: boolean;
}

export interface Ram {
  upperLimit: number;
  lowerLimit: number;
  thresholdPolicy: string;
  ramScalingEnabled: boolean;
}

export interface Latency {
  upperLimit: number;
  lowerLimit: number;
  thresholdPolicy: string;
  latencyScalingEnabled: boolean;
}

export interface Requests {
  quotient: number;
  minQuotient: number;
  thresholdPolicy: string;
  quotientScalingEnabled: boolean;
}

export interface Learning {
  learningEnabled: boolean;
  learningTimeMultiplier: number;
  learningStartTime: number;
  learningStartTimeAsString: Date;
}
