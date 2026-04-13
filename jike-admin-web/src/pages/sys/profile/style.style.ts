import { createStyles } from "antd-style";

const useStyles = createStyles(({ token }) => {
  return {
    main: {
      display: "flex",
      width: "100%",
      height: "100%",
    },
    leftMenu: {
      width: "224px",
      borderRight: `${token.lineWidth}px solid ${token.colorSplit}`,
      ".ant-menu-inline": { border: "none" },
      ".ant-menu-horizontal": { fontWeight: "bold" },
      [`@media screen and (max-width: ${token.screenMD}px)`]: {
        width: "100%",
        border: "none",
      },
    },
    right: {
      flex: "1",
      padding: "8px 40px",
    },
    title: {
      marginBottom: "12px",
      color: token.colorTextHeading,
      fontWeight: "500",
      fontSize: "20px",
      lineHeight: "28px",
    },
  };
});

export default useStyles;
