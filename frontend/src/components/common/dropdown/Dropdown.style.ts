import styled, { keyframes } from "styled-components";
import { Z_INDEX } from "@/styles/zIndex";

const dropdown = keyframes`
  0% {
    transform: translateY(-10%);
    opacity: 0;
  }
  100% {
    transform: translateY(0);
    opacity: 1;
  }
`;

export const DropdownContainer = styled.div`
  cursor: pointer;
  position: relative;
  width: 160px;
  height: 40px;
`;

export const DropdownToggle = styled.button<{ $error: boolean }>`
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 100%;
  height: 100%;
  padding: 0.6rem;

  font: ${({ theme }) => theme.TEXT.small};
  color: ${({ theme }) => theme.COLOR.grey4};

  background-color: transparent;
  border: 1px solid ${(props) => (props.$error ? props.theme.COLOR.error : props.theme.COLOR.grey1)};
  border-radius: 6px;
`;

export const DropdownMenu = styled.div`
  position: absolute;
  z-index: ${Z_INDEX.dropdown};
  right: 0;

  display: flex;
  flex-direction: column;

  width: 100%;

  background-color: white;
  border: 1px solid ${({ theme }) => theme.COLOR.grey1};
  border-radius: 4px;

  animation: ${dropdown} 0.4s ease;
`;

export const DropdownItemWrapper = styled.ul`
  margin: 0.6rem;
`;

export const DropdownItem = styled.li<{ $isSelected: boolean }>`
  cursor: pointer;

  display: flex;
  align-items: center;

  padding: 0.8rem 0;

  background-color: ${(props) => (props.$isSelected ? props.theme.COLOR.grey0 : "")};

  transition: background-color 0.3s;

  &:hover {
    background-color: ${({ theme }) => theme.COLOR.grey0};
  }

  span {
    font: ${({ theme }) => theme.TEXT.small};
    color: ${({ theme }) => theme.COLOR.grey4};
  }
`;
