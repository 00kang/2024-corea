import { Classification } from "@/@types/roomInfo";

export const convertClassificationToKorean = (text: Classification) => {
  switch (text) {
    case "ALL":
      return "전체";
    case "ANDROID":
      return "안드로이드";
    case "BACKEND":
      return "백엔드";
    case "FRONTEND":
      return "프론트엔드";
  }
};

export const convertedDayToKorean = (dDay: string) => {
  const leftDay = dDay.split("-")[1];

  return `${leftDay}일 전`;
};
