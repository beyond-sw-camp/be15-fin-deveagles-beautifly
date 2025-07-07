"""
Analytics Dashboard Package

비즈니스 인텔리전스 대시보드 패키지 최상위 모듈.
`BusinessIntelligenceDashboard` 는 `bi_dashboard` 모듈에서 제공하며
기존 경로인 `analytics.dashboard.cohort_dashboard` 도 하위호환을 위해
모듈 alias 로 지원한다.
"""

from importlib import import_module
import sys as _sys
from types import ModuleType as _ModuleType

# --- Canonical implementation module ---
_bi_mod: _ModuleType = import_module(__name__ + '.bi_dashboard')
BusinessIntelligenceDashboard = _bi_mod.BusinessIntelligenceDashboard  # type: ignore[attr-defined]

# --- Backward-compat module aliases ----
# analytics.dashboard.cohort_dashboard -> analytics.dashboard.bi_dashboard
# _sys.modules[__name__ + '.cohort_dashboard'] = _bi_mod

# Older class alias
# CohortDashboard = BusinessIntelligenceDashboard

__all__ = ['BusinessIntelligenceDashboard'] 