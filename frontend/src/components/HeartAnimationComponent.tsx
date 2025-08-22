import React, { useEffect, useState } from 'react';
import { HeartAnimation } from '../types';

interface HeartAnimationProps {
  heart: HeartAnimation;
}

const HeartAnimationComponent: React.FC<HeartAnimationProps> = ({ heart }) => {
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setVisible(false);
    }, 2500);

    return () => clearTimeout(timer);
  }, []);

  if (!visible) return null;

  return (
    <div
      className="animate-heart-float"
      style={{
        left: heart.x - 15,
        top: heart.y - 15,
      }}
    >
      ❤️
    </div>
  );
};

export default HeartAnimationComponent;