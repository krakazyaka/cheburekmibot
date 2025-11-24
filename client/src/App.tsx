import { Switch, Route } from "wouter";
import { queryClient } from "./lib/queryClient";
import { QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "@/components/ui/toaster";
import { TooltipProvider } from "@/components/ui/tooltip";
import MenuPage from "@/pages/MenuPage";
import { CartProvider } from "@/contexts/CartContext";
import { TelegramProvider } from "@/contexts/TelegramContext";
import LoyaltyPage from "@/pages/LoyaltyPage";
import OrdersPage from "@/pages/OrdersPage";
import ProfilePage from "@/pages/ProfilePage";
import CheckoutPage from "@/pages/CheckoutPage";
import ConfirmationPage from "@/pages/ConfirmationPage";
import AdminPage from "@/pages/AdminPage";
import AdminLoginPage from "@/pages/AdminLoginPage";
import BottomNav from "@/components/BottomNav";
import NotFound from "@/pages/not-found";
import { useLocation } from "wouter";

function Router() {
  const [location] = useLocation();
  const isAdminPage = location === "/admin" || location === "/admin/login";

  return (
    <CartProvider>
      <Switch>
        <Route path="/" component={MenuPage} />
        <Route path="/loyalty" component={LoyaltyPage} />
        <Route path="/orders" component={OrdersPage} />
        <Route path="/profile" component={ProfilePage} />
        <Route path="/checkout" component={CheckoutPage} />
        <Route path="/confirmation" component={ConfirmationPage} />
        <Route path="/admin/login" component={AdminLoginPage} />
        <Route path="/admin" component={AdminPage} />
        <Route component={NotFound} />
      </Switch>
      {!isAdminPage && <BottomNav />}
    </CartProvider>
  );
}

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <TelegramProvider>
        <TooltipProvider>
          <Toaster />
          <Router />
        </TooltipProvider>
      </TelegramProvider>
    </QueryClientProvider>
  );
}

export default App;
