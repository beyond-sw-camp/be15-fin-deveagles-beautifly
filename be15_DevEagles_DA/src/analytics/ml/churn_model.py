from __future__ import annotations

"""Stub churn prediction model.
In production this module will load a trained ML model and perform inference.
For now it returns a simple heuristic risk score in 0-100 range and risk level string.
"""

from typing import Tuple

import numpy as np
import pandas as pd


def predict(df: pd.DataFrame) -> Tuple[pd.Series, pd.Series]:
    """Return churn risk score (0-100) and level for each row.

    Heuristic: customers with recent visits > 180 days ago get higher score.
    """
    if "days_since_last_visit" not in df.columns:
        scores = pd.Series(np.zeros(len(df)), index=df.index)
    else:
        max_days = df["days_since_last_visit"].fillna(0).max() or 1
        scores = (df["days_since_last_visit"].fillna(0) / max_days) * 100

    def level(score: float) -> str:
        if score >= 70:
            return "high"
        if score >= 40:
            return "medium"
        return "low"

    levels = scores.apply(level)
    return scores, levels 