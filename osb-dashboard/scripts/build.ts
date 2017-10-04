/* tslint:disable:no-console */
import * as spawn from 'cross-spawn';
import * as program from 'commander';

function compile(buildTarget: string, cb) {
  const isRunningInCi = !!process.env['TARGET'];
  const target = process.env['TARGET'] || 'production';
  const aot = process.env['AOT'] || 'true';

  const outFolder = './dist/' + buildTarget;

  const c = {
    cmd: 'ng',
    args: [
      'build',
      '--target=' + target,
      '--environment=' + buildTarget,
      '--app=' + buildTarget,
      '--aot=' + aot,
      '--progress=' + !isRunningInCi,
      '--output-path=' + outFolder,
      '--delete-output-path=true', // delete before build
      '--stats-json=true'
    ]
  };

  console.log(`Building: ${JSON.stringify(c.args.slice(1))}`);
  const child = spawn.spawn(c.cmd, c.args);

  child.stdout.pipe(process.stdout);
  child.stderr.pipe(process.stderr);


  child.on('exit', (code) => {
    if (code !== 0) {
      cb(code);
      return;
    }
  });
}

program
  .version('0.0.1')
  .option('-t, --target <env>', 'Build Target (angular-cli environment)')
  .parse(process.argv);

compile(program['target'], (code) => {
  process.exit(code);
});
