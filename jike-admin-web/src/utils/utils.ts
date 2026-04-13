import md5 from "crypto-js/md5";

export const isWeChatBrowser = () => {
  const userAgent = navigator.userAgent.toLowerCase();
  return userAgent.indexOf("micromessenger") !== -1;
};

export const MD5 = (str: string) => {
  return md5(str).toString();
};

export const md5Pwd = (pwd: string) => {
  return md5(md5(pwd).toString()).toString();
};
