
const debug = process.env.NODE_ENV !== 'production'
const API_ROOT  = "/v2/";

// 屏幕尺寸，bootstrap设定的几个尺寸
const screenSizes = { xs: 480, sm: 768, md: 992, lg: 1200 };

// 是否hover展开侧边栏
const sidebarExpandOnHover = true;
const fixedSidebar = sidebarExpandOnHover;
const navbarMenuHeight = '200px'; //The height of the inner menu
//General animation speed for JS animated elements such as box collapse/expand and
//sidebar treeview slide up/down. This options accepts an integer as milliseconds,
//'fast', 'normal', or 'slow'
const navbarMenuSlimscrollWidth = '3px'; //The width of the scroll bar

const qiniuPath = "http://qiniu.adcore.i-xad.com";
const uploadServer = 'http://upload.qiniu.com';
export { debug, API_ROOT, screenSizes, sidebarExpandOnHover, fixedSidebar, navbarMenuHeight, navbarMenuSlimscrollWidth, qiniuPath, uploadServer };