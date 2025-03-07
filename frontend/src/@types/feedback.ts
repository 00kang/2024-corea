import { NonEmptyArray } from "@/@types/NonEmptyArray";

export interface FeedbackCardDataList {
  readonly roomId: number;
  readonly title: string;
  readonly roomKeywords: NonEmptyArray<string>;
  readonly developFeedback: FeedbackCardData[];
  readonly socialFeedback: FeedbackCardData[];
}

export interface FeedbackCardData {
  readonly feedbackId: number;
  readonly profile: string;
  readonly roomId: number;
  readonly receiverId: number;
  readonly username: string;
  readonly feedbackKeywords: NonEmptyArray<string>;
  readonly feedbackText: string;
  readonly evaluationPoint: 1 | 2 | 3 | 4 | 5;
  readonly isWrited: boolean;
  readonly link: string;
}

export interface RevieweeFeedbackData {
  feedbackId: number;
  receiverId: number;
  evaluationPoint: number;
  feedbackKeywords: string[];
  feedbackText: string;
  recommendationPoint: number;
}

export type ReviewerFeedbackData = Omit<RevieweeFeedbackData, "recommendationPoint">;
