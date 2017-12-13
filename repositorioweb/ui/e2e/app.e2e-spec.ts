import { SegpresPage } from './app.po';

describe('segpres App', () => {
  let page: SegpresPage;

  beforeEach(() => {
    page = new SegpresPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Welcome to app!');
  });
});
