declare module "@/*";

interface IRouterItem {
  index?: boolean;
  path?: string;
  element: React.ComponentType | React.ReactNode;
  children?: IRouterItem[];
  permission?: string;
}

interface IMenu {
  id: number;
  name: string;
  pid: number;
  route: string?;
  code: string;
  type: "MENU" | "FUNC" | "LOGIN";
}
