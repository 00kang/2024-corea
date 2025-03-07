import { useState } from "react";

const useSelectedCategory = (initialCategory: string = "all") => {
  const [selectedCategory, setSelectedCategory] = useState(() => {
    return sessionStorage.getItem("selectedCategory") || initialCategory;
  });

  const handleSelectedCategory = (category: string) => {
    setSelectedCategory(category);
    sessionStorage.setItem("selectedCategory", category);
  };

  return { selectedCategory, handleSelectedCategory };
};

export default useSelectedCategory;
