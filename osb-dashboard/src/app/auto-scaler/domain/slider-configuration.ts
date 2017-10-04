export class SliderConfiguration {
    public static readonly RANGE = 10;
    public static readonly MIN_MAX_CONFIG: any = {
        behaviour: 'drag',
        connect: true,
        margin: 1,
        step: 1,
        range: {
            min: 1,
            max: 50
        },
        pips: {
            mode: 'steps',
            density: 1
        }
    };

    public static readonly LOWER_UPPER_CONFIG: any = {
        behaviour: 'drag',
        connect: true,
        margin: 5,
        step: 5,
        range: {
            min: 0,
            max: 100
        },
        pips: {
            mode: 'steps',
            density: 5
        }
    };
}
