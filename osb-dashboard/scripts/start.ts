/* tslint:disable:no-console */
import * as spawn from 'cross-spawn';
import * as program from 'commander';

function serve(buildTarget: string, cb) {
  const target = process.env['TARGET'] || 'development';
  const aot = process.env['AOT'] || 'false';

  const c = {
    cmd: 'ng',
    args: [
      'serve',
      '--target=' + target,
      '--environment=' + buildTarget,
      '--app=' + buildTarget,
      '--aot=' + aot,
      '--port', '9001'
    ]
  };

  console.log(`Serving: ${JSON.stringify(c.args.slice(1))}`);
  const child = spawn.spawn(c.cmd, c.args);

  child.stdout.pipe(process.stdout);
  child.stderr.pipe(process.stderr);

  child.on('exit', (code) => {
    cb(code);
  });
}

program
  .version('0.0.1')
  .option('-t, --target <env>', 'Build Target (angular-cli environment)')
  .parse(process.argv);

serve(program['target'], (code) => {
  process.exit(code);
});
