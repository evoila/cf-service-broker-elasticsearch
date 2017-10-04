import { SbDashboardPage } from './app.po';

describe('sb-dashboard App', () => {
  let page: SbDashboardPage;

  beforeEach(() => {
    page = new SbDashboardPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
