import { useState } from "react";
import CategoryChips from "../CategoryChips";

export default function CategoryChipsExample() {
  const [selected, setSelected] = useState("all");
  return (
    <div className="p-4">
      <CategoryChips
        selectedCategory={selected}
        onSelectCategory={setSelected}
      />
    </div>
  );
}
