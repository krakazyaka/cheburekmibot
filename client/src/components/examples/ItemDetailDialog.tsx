import { useState } from "react";
import ItemDetailDialog from "../ItemDetailDialog";
import { Button } from "@/components/ui/button";
import cheburekImage from "@assets/generated_images/cheburek_menu_item_photo_39876ff1.png";

export default function ItemDetailDialogExample() {
  const [open, setOpen] = useState(false);

  return (
    <div className="p-4">
      <Button onClick={() => setOpen(true)}>Open Item Detail</Button>
      <ItemDetailDialog
        open={open}
        onClose={() => setOpen(false)}
        item={{
          id: "1",
          name: "Classic Cheburek",
          description:
            "Traditional fried pastry filled with seasoned meat and onions, crispy and golden brown. A beloved Eastern European delicacy.",
          price: 5.99,
          image: cheburekImage,
        }}
        onAddToCart={(qty) => console.log(`Added ${qty} to cart`)}
      />
    </div>
  );
}
