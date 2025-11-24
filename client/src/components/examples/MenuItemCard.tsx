import MenuItemCard from "../MenuItemCard";
import cheburekImage from "@assets/generated_images/cheburek_menu_item_photo_39876ff1.png";

export default function MenuItemCardExample() {
  return (
    <div className="p-4 max-w-md">
      <MenuItemCard
        id="1"
        name="Classic Cheburek"
        description="Traditional fried pastry filled with seasoned meat and onions, crispy and golden"
        price={5.99}
        image={cheburekImage}
        onClick={() => console.log("Item clicked")}
      />
    </div>
  );
}
